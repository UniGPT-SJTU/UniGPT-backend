package com.ise.unigpt.dto;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityDTO {
    private String kind;
    private boolean isDefault;
    private String code;
    private String userType;
    private String userTypeName;
    private OrganizeDTO organize;
    private OrganizeDTO topOrganize;
    private OrganizeDTO mgtOrganize;
    private String status;
    private String expireDate;
    private long createDate;
    private long updateDate;
    private String gjm;
    private boolean defaultOptional;
    private String admissionDate;
    private String trainLevel;
    private String graduateDate;
    private List<OrganizeDTO> topOrganizes;
}
