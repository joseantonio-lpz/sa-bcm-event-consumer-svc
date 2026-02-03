package mx.com.seccionamarilla.bcm.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.exception.InternalServiceException;
import mx.com.seccionamarilla.bcm.model.dto.Business;
import mx.com.seccionamarilla.bcm.model.dto.BusinessResponse;
import mx.com.seccionamarilla.bcm.model.dto.KafkaSubmitRequest;
import mx.com.seccionamarilla.bcm.model.dto.KafkaWfmBcmRequest;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.service.IBcmOlbcService;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Service
public class BcmOlbcServiceImpl implements IBcmOlbcService {

	private final IProcessMessagesService processMessagesService;
	private static final String VAR_FLOW_INSTANCE_ID = "FLOW_INSTANCE_ID";
	private final WebClient businessWebClient;

	@Override
	public void updateFlowTask(KafkaWfmBcmRequest kTaskRequest, ProcessedMessage pm, String taskStatus) {
		try {

			// No se actualizó nada → revisar criterios
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "PROCESADO", null, VAR_FLOW_INSTANCE_ID,
					kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(), null);
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
			BusinessResponse negocio = getBusinessVersion(Long.valueOf(businessRequest.getExternalId()));
			if (negocio != null) {
				Business negocioActivo = getActiveBusiness(negocio);
				if (negocioActivo != null) {
					Integer versionNumber = negocioActivo.getBusinessVersionDetail().getVersionNumber();
					updateBusinessState(businessRequest.getExternalId(), versionNumber, "READY").block();
				}
			}
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "PROCESADO", null, "BUSINESS_ID",
					businessRequest.getExternalId(), businessRequest.getFormCode(), null);
		} catch (Exception e) {
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(), "BUSINESS_ID",
					businessRequest.getExternalId(), businessRequest.getFormCode(), e.getLocalizedMessage());
		}
	}

	private Mono<Void> updateBusinessState(
            String businessId,
            Integer versionNumber,
            String state) {
        return businessWebClient
                .method(HttpMethod.PATCH)
                .uri("/api/v1/businesses/{businessId}/VersionNumber/{versionNumber}/state/{state}",
                        businessId, versionNumber, state)
                .retrieve()
                .bodyToMono(Void.class);
    }
	
	private BusinessResponse getBusinessVersion(Long businessId) {
		return businessWebClient
				.get()
				.uri("/api/v1/businesses/{id}/version", businessId)
				.retrieve()
				.bodyToMono(BusinessResponse.class)
				.block();
	}

	private Business getActiveBusiness(BusinessResponse response) {
		return response.getBusinesses()
				.stream()
				.filter(b -> "ACTIVE".equalsIgnoreCase(b.getBusinessStatus()))
				.findFirst()
				.orElse(null); // o lanza excepción si prefieres
	}
}
