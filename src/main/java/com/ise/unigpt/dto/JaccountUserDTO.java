package com.ise.unigpt.dto;

import lombok.Data;
import java.util.List;

@Data
public class JaccountUserDTO {
    private String id;
    private String account;
    private String name;
    private String kind;
    private String code;
    private String userType;
    private OrganizeDTO organize;
    private OrganizeDTO topOrganize;
    private String classNo;
    private String gender;
    private String email;
    private int timeZone;
    private String mobile;
    private List<IdentityDTO> identities;
    private String unionId;
    private String accountExpireDate;
    // ... other fields ...
}
