package com.ecom.springai.helper;

import org.springframework.core.io.ClassPathResource;
import org.stringtemplate.v4.ST;

import java.nio.file.Files;

public class PromptBuilder {

    public static String buildUserPrompt(int sectionNumber, int videoNumber, String courseTitle, String title, String transcript) throws Exception {
        var res = new ClassPathResource("promptTemplate/userPrompt.st");
        String templateStr = Files.readString(res.getFile().toPath());
        ST st = new ST(templateStr);
        st.add("sectionNumber", sectionNumber);
        st.add("videoNumber", videoNumber);
        st.add("title", title == null ? "" : title);
        st.add("transcript", transcript);
        return st.render();
    }
}
