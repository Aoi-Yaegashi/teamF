package com.example.raffinehome.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.stream.Collectors;

@ControllerAdvice(basePackages = "com.example.raffinehome.admin.controller")
public class AdminExceptionHandler {

    // バリデーション例外(MethodArgumentNotValidException)対応
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        // 全エラーのメッセージをまとめて返す場合
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.joining("・"));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorMsg);
    }

    // リクエストbodyの変換失敗(HttpMessageNotReadableException)対応
    @ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof InvalidFormatException ife) {
        for (var ref : ife.getPath()) {
            String field = ref.getFieldName();
            if ("price".equals(field)) {
                return ResponseEntity.badRequest().body("priceは正しい数値で指定してください。");
            } else if ("salePrice".equals(field)) {
                return ResponseEntity.badRequest().body("salePriceは正しい数値で指定してください。");
            } else if ("stockQuantity".equals(field)) {
                return ResponseEntity.badRequest().body("stockQuantityは正しい数値で指定してください。");
            }
            // 必要があれば他フィールドも追加
        }
    }
    return ResponseEntity.badRequest().body("リクエスト値の形式が正しくありません。");
}

    // パラメータ変換失敗（数字でないなど）への対応例
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("リクエストパラメータの型が不正です。");
    }

    // パラメータ不足（例: @RequestParamの必須値無し）
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("必須パラメータが不足しています。");
    }

    // その他バリデーション例外
    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindException(BindException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.joining("・"));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorMsg);
    }
}
