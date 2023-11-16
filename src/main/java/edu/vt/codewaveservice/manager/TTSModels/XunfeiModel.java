package edu.vt.codewaveservice.manager.TTSModels;

import edu.vt.codewaveservice.utils.XunFeiUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component("xunfei")
public class XunfeiModel implements TTSModel{
    @Override
    public String generateAudio(String text) throws IOException {
        try {
            return XunFeiUtil.convertText(text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
