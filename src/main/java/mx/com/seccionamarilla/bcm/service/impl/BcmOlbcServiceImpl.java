package mx.com.seccionamarilla.bcm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.exception.InternalServiceException;
import mx.com.seccionamarilla.bcm.model.dto.Business;
import mx.com.seccionamarilla.bcm.model.dto.BusinessReferenceResponse;
import mx.com.seccionamarilla.bcm.model.dto.BusinessResponse;
import mx.com.seccionamarilla.bcm.model.dto.KafkaSubmitRequest;
import mx.com.seccionamarilla.bcm.model.dto.KafkaWfmBcmRequest;
import mx.com.seccionamarilla.bcm.model.dto.LoginByTokenRequest;
import mx.com.seccionamarilla.bcm.model.dto.LoginByTokenResponse;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.service.IBcmOlbcService;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BcmOlbcServiceImpl implements IBcmOlbcService {

	@Autowired
	private IProcessMessagesService processMessagesService;
	private static final String VAR_FLOW_INSTANCE_ID = "FLOW_INSTANCE_ID";
	
	@Autowired
	@Qualifier("basicAuthWebClient")
	private WebClient businessWebClient;
	
	@Autowired
	@Qualifier("authTokenWebClient")
	private WebClient tokenWebClient;
	
	@Value("${bcm-event-properties.token}")
	private String token;
	
	@Override
	public void updateBusinessStatus(KafkaWfmBcmRequest kTaskRequest, ProcessedMessage pm, String status) {
		log.info("STATUS: " + status);
		try {
			LoginByTokenResponse loginResponse = loginByToken(token).block();
			String bearerToken = loginResponse.getBearerToken();
			log.info("bearerToken: " + bearerToken);
			BusinessReferenceResponse response = getBusinessesByReference(bearerToken, "folio-contrato",
					kTaskRequest.getExternalId()).block();
			response.getBusinesses().forEach(br -> {
				log.info("Encontro negocios ");
				Long procesamiento = System.currentTimeMillis();
				processMessagesService.insertCompleto(pm, procesamiento);
				log.info("procesamiento " + System.currentTimeMillis());
				String businessId = String.valueOf(br.getBusinessId());
				Integer versionNumber = br.getVersionNumber();
				String respuestaCorrecta = updateBusinessState(businessId, versionNumber, status, procesamiento, pm.getProcessedMessagesId());
				if (respuestaCorrecta != null) {
					// Se crea registro log por actualizacion
					processMessagesService.updateStatusOut(procesamiento, "PROCESADO", null, "PROCESSED_MESSAGES_ID",
							String.valueOf(pm.getProcessedMessagesId()), kTaskRequest.getProductCode(),
							respuestaCorrecta);
				}
			});
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "PROCESADO", null,
					VAR_FLOW_INSTANCE_ID, kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(), null);
		} catch (DataAccessException dae) {
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", dae.getMessage(),
					VAR_FLOW_INSTANCE_ID, kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(),
					dae.getLocalizedMessage());
			log.error("Ocurrio el siguiente error en el servicio guardar new_flow_task: ", dae.getMessage(), dae);
			throw new InternalServiceException("Error al guardar new_flow_task en la base de datos: ", dae);
		} catch (Exception e) {
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(),
					VAR_FLOW_INSTANCE_ID, kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(),
					e.getLocalizedMessage());
			log.error("Error inesperado en newFlowTask: {}", e.getMessage(), e);
			throw new InternalServiceException("Error inesperado al guardar new_flow_task: " + e.getMessage(), e);
		}

	}

	@Override
	public void updateBusiness(KafkaSubmitRequest businessRequest, ProcessedMessage pm) {
		try {
			BusinessResponse negocio = getBusinessVersion(businessRequest.getExternalId(), pm.getProcessedMessagesId());
			if (negocio != null) {
				Business negocioActivo = getActiveBusiness(negocio, pm.getProcessedMessagesId());
				if (negocioActivo != null) {
					Integer versionNumber = negocioActivo.getBusinessVersionDetail().getVersionNumber();
					String respuestaCorrecta = updateBusinessState(businessRequest.getExternalId(), versionNumber, "READY", pm.getProcessedMessagesId(), null);
					processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "PROCESADO", null,
							"BUSINESS_ID", businessRequest.getExternalId(), businessRequest.getFormCode(), respuestaCorrecta);
				}
			}

		} catch (Exception e) {
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(), "BUSINESS_ID",
					businessRequest.getExternalId(), businessRequest.getFormCode(), e.getLocalizedMessage());
		}
	}

	public Mono<LoginByTokenResponse> loginByToken(String token) {
	    return tokenWebClient.post()
	            .uri("/api/Auth/login-by-token")
	            .bodyValue(new LoginByTokenRequest(token))
	            .retrieve()
	            .bodyToMono(LoginByTokenResponse.class);
	}
	
	private String updateBusinessState(
            String businessId,
            Integer versionNumber,
            String state, Long processedMessagesId, Long idRef) {
		try {
        return businessWebClient
                .method(HttpMethod.PATCH)
                .uri("/api/v1/businesses/{businessId}/VersionNumber/{versionNumber}/state/{state}",
                        businessId, versionNumber, state)
                .retrieve()
                .bodyToMono(String.class)
                .block();
	} catch (Exception e) {
		log.error("error:" + e.getMessage());
		if (idRef == null) {
			processMessagesService.updateStatusOut(processedMessagesId, "ERROR", e.getMessage(), "BUSINESS_ID",
					businessId, businessId, "API_BUSINESS");
		} else {
			processMessagesService.updateStatusOut(processedMessagesId, "ERROR", e.getMessage(),
					"PROCESSED_MESSAGES_ID", String.valueOf(idRef), businessId, "API_BUSINESS");
		}
		return null;
	}
    }
	
	private BusinessResponse getBusinessVersion(String businessId, Long processedMessagesId) {
		try {
		return businessWebClient
				.get()
				.uri("/api/v1/businesses/{id}/version", businessId)
				.retrieve()
				.bodyToMono(BusinessResponse.class)
				.block();
		}catch(Exception e) {
			processMessagesService.updateStatusOut(processedMessagesId, "ERROR", e.getMessage(), "BUSINESS_ID",
					businessId, businessId, "API_BUSINESS");
			return null;
		}
	}

	private Business getActiveBusiness(BusinessResponse response, Long processedMessagesId) {
		try {
			return response.getBusinesses()
					.stream()
					.filter(b -> "ACTIVE".equalsIgnoreCase(b.getBusinessStatus()))
					.findFirst()
					.orElse(null); // o lanza excepción si prefieres
		}catch(Exception e) {
			processMessagesService.updateStatusOut(processedMessagesId, "ERROR", e.getMessage(), "BUSINESS_ID",
					response.getBusinessId(), response.getBusinessId(), "NEGOCIO_ACTIVO");
			return null;
		}		
	}
	
	private Mono<BusinessReferenceResponse> getBusinessesByReference(
            String bearerToken,
            String refKey,
            String refValue) {

        return businessWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/businesses/references")
                        .queryParam("refKey", refKey)
                        .queryParam("refValue", refValue)
                        .build())
                .headers(h -> h.setBearerAuth(bearerToken))
                .retrieve()
                .bodyToMono(BusinessReferenceResponse.class);
    }
}

