package org.blokaj.models.responses;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private Integer code;

    private String status;

    private T data;
}
