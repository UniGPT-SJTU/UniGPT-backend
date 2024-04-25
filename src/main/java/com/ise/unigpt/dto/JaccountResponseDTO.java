package com.ise.unigpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
/**
 * Jaccount返回的用户信息
 * example:
 * {
    "errno": 0,
    "error": "success",
    "total": 0,
    "entities": [
        {
            "id": "",
            "account": "",
            "name": "",
            "kind": "canvas.profile",
            "code": "",
            "userType": "student",
            "organize": {
                "name": "电子信息与电气工程学院",
                "id": "03000"
            },
            "topOrganize": {
                "name": "电子信息与电气工程学院",
                "id": "03000"
            },
            "classNo": "电院22",
            "gender": "male",
            "email": "7@qq.com",
            "timeZone": 0,
            "mobile": "13",
            "identities": [
                {
                    "kind": "nicman.identity",
                    "isDefault": false,
                    "code": "202235",
                    "userType": "freshman",
                    "userTypeName": "新生",
                    "organize": {
                        "name": "教务处",
                        "id": "60200"
                    },
                    "topOrganize": {
                        "name": "教务处",
                        "id": "60200"
                    },
                    "mgtOrganize": {
                        "name": "教务处",
                        "id": "60200"
                    },
                    "status": "正常",
                    "expireDate": "2022-10-30",
                    "createDate": 1659492695,
                    "updateDate": 1659492695,
                    "gjm": "156",
                    "defaultOptional": false,
                    "admissionDate": "2022-08-02",
                    "trainLevel": "新生",
                    "graduateDate": "2022-10-30",
                    "topOrganizes": [
                        {
                            "name": "教务处",
                            "id": "60200"
                        }
                    ]
                },
                {
                    "kind": "nicman.identity",
                    "isDefault": true,
                    "code": "5220",
                    "userType": "student",
                    "userTypeName": "学生",
                    "organize": {
                        "name": "电子信息与电气工程学院",
                        "id": "03000"
                    },
                    "topOrganize": {
                        "name": "电子信息与电气工程学院",
                        "id": "03000"
                    },
                    "mgtOrganize": {
                        "name": "电子信息与电气工程学院",
                        "id": "03000"
                    },
                    "topMgtOrganize": {
                        "name": "电子信息与电气工程学院",
                        "id": "03000"
                    },
                    "status": "正常",
                    "expireDate": "2026-06-30",
                    "createDate": 1659604564,
                    "updateDate": 1704340655,
                    "classNo": "电院22",
                    "gjm": "156",
                    "defaultOptional": true,
                    "major": {
                        "name": "软件工程",
                        "id": "0806050037"
                    },
                    "admissionDate": "2022-08-23",
                    "trainLevel": "本科生",
                    "graduateDate": "2026-06-30",
                    "topOrganizes": [
                        {
                            "name": "电子信息与电气工程学院",
                            "id": "03000"
                        }
                    ],
                    "type": {
                        "id": "111",
                        "name": "本科生(中国国籍)"
                    }
                }
            ],
            "unionId": "oGEgEwgAuU1",
            "accountExpireDate": "2026-12-31"
        }
    ]
}
 */

@Data
public class JaccountResponseDTO {
    private int errno;
    private String error;
    private int total;
    @JsonProperty("entities")
    private List<JaccountUserDTO> users;

}
