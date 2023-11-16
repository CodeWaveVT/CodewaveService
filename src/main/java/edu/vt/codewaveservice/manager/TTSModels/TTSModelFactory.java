package edu.vt.codewaveservice.manager.TTSModels;


import jodd.bean.BeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TTSModelFactory {

    @Resource
    private ApplicationContext applicationContext;

    public TTSModel getModel(String modelType){
        try{
            return applicationContext.getBean(modelType,TTSModel.class);
        }catch (BeanException e){
            throw new RuntimeException("no such model type");
        }
    }
}
