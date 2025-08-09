package ru.yandex.practicum.filmorate.common;

import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public class CommonUtility {
    public static int getIdFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
        String[] contentArray = result.getResponse().getContentAsString().split(",");
        String[] idObj = contentArray[0].split(":");
        return Integer.parseInt(idObj[1]);
    }
}
