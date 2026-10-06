package com.green.spring_board.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    //성공 + 데이터 o
    public static <T> ApiResponse<T> ok(T data){
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();

        //return new ApiResponse<>(true, data);
    }
    //성공 + 데이터 x
   public static <T> ApiResponse<T> ok(){
        return ApiResponse.<T>builder()
                .success(true)
                .build();
   }

    //실패
    public static <T> ApiResponse<T> fail(String message){
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
