package com.ise.unigpt.dto;

import lombok.Data;
import java.util.List;

@Data
public class IdentityDTO {
    private String kind;
    private boolean isDefault;
    private String code;
    private String userType;
    private String userTypeName;
    private Organize organize;
    private Organize topOrganize;
    private Organize mgtOrganize;
    private String status;
    private String expireDate;
    private long createDate;
    private long updateDate;
    private String gjm;
    private boolean defaultOptional;
    private String admissionDate;
    private String trainLevel;
    private String graduateDate;
    private List<Organize> topOrganizes;
}
