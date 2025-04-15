package com.hc.stackoverflow.entity.dto;


import lombok.*;

import java.util.Map;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private Map<String,String> errorMessage;
}
