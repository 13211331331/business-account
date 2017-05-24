package com.geekcap.javaworld.springbatchexample.simple.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Created by hanlin.huang on 2017/5/19.
 */
public class Task03 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepcontribution, ChunkContext chunkcontext) throws Exception {
        System.out.println("doing Task03......................");
        return RepeatStatus.FINISHED;
    }
}
