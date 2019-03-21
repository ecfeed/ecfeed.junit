package com.ecfeed.junit.main.processor;

import com.ecfeed.core.model.ChoiceNode;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TupleProcessorStatic {


    private final Optional<List<List<ChoiceNode>>> fList;
    private final Consumer<List<ChoiceNode>> fConsumer;

    public TupleProcessorStatic(Optional<List<List<ChoiceNode>>> list, Consumer<List<ChoiceNode>> consumer) {
        fList = list;
        fConsumer = consumer;
    }

    public void process() {
        if (!fList.isPresent()) {
            return;
        }

        fList.get().stream().forEach(fConsumer::accept);
    }

}
