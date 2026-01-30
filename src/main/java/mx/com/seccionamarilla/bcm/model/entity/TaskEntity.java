package mx.com.seccionamarilla.bcm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    private Long    adnProductId;
    private String  flowCode;
    private String  taskCode;
    private String  flowStatus;
    private String  taskStatus;
    private String  activityCode;
    private Long    assigneeUser;
    private Long    adnEntityId;
    private Long    ctcrSysUserId;
    private String  error_desc;
}
