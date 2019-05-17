package com.ecfeed.junit.main.processor;

import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.ChoiceNode;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class TupleProcessorDynamic {

    private final Optional<AbstractAlgorithm<ChoiceNode>> fGenerator;
    private final Consumer<List<ChoiceNode>> fConsumer;

    public TupleProcessorDynamic(Optional<AbstractAlgorithm<ChoiceNode>> generator, Consumer<List<ChoiceNode>> consumer) {
        fGenerator = generator;
        fConsumer = consumer;
    }

    public void process() {
        if (!fGenerator.isPresent()) {
            return;
        }

        List<ChoiceNode> tuple = null;

        while(true)
        {
            try {
                tuple = fGenerator.get().getNext();
                if(tuple==null)
                    break;
                fGenerator.get().getConstraintEvaluator().adapt(tuple);
            } catch (GeneratorException e) {
                e.printStackTrace();
            }
            fConsumer.accept(tuple);
        }
    }

}
