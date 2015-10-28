package org.ikasan.flow.event;

import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvocation;

/**
 * Simple static factory for creating FlowElementInvocation objects
 */
public class FlowElementInvocationFactory {

    private FlowElementInvocationFactory(){}

    /**
     * Returns a new FlowElementInvocation object
     * @return a new FlowElementInvocation object
     */
    public static FlowElementInvocation newInvocation()
    {
        return new DefaultFlowElementInvocation();
    }

    /**
     * Default implementation of the FlowElementInvocation
     */
    public static class DefaultFlowElementInvocation implements FlowElementInvocation
    {
        /** the start and end times (epoch) of the FlowElement invocation */
        private volatile long startTime, endTime;

        /** handle to the FlowElement this is invoked */
        private FlowElement flowElement;

        /** the FlowEvent IDENTIFIER */
        private Object identifier;

        @Override
        public void beforeInvocation(FlowElement flowElement) {
            startTime = System.currentTimeMillis();
            this.flowElement = flowElement;
        }

        @Override
        public void afterInvocation(FlowElement flowElement) {
            endTime = System.currentTimeMillis();
        }

        @Override
        public FlowElement getFlowElement() {
            return flowElement;
        }

        @Override
        public long getStartTime() {
            return startTime;
        }

        @Override
        public long getEndTime() {
            return endTime;
        }

        @Override
        public Object getIdentifier() {
            return identifier;
        }

        @Override
        public void setIdentifier(Object identifier) {
            this.identifier = identifier;
        }
    }
}
