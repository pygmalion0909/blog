package com.example.rsocketapi.rsocketTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data  
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    String username;
    String message;
}
