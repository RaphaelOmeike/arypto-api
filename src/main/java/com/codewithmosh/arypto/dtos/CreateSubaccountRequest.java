package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CreateSubaccountRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

}
