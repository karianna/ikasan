package org.ikasan.framework.initiator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowInvocationContext;
import org.ikasan.framework.monitor.MonitorListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class AbstractInitiatorTest
{

    
    /**
     * JMock Mockery
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    
    /**
     * mock MonitorListener
     */
    private MonitorListener firstMonitorListener = mockery.mock(MonitorListener.class, "firstMonitorListener");
 
    /**
     * mock MonitorListener
     */
    private MonitorListener secondMonitorListener = mockery.mock(MonitorListener.class, "secondMonitorListener");

    /**
     * name of the module
     */
    private String moduleName = "moduleName";
    
    /**
     * name of the initiator
     */
    private String initiatorName = "initiatorName";
 
    /**
     * mocked Flow
     */
    private Flow flow = mockery.mock(Flow.class);
    
    /**
     * List of Events to play
     */
    private List<Event> eventsToPlay = new ArrayList<Event>();
    
    /**
     * mocked Event to play
     */
    private Event event1 = mockery.mock(Event.class, "Event1");
    
    /**
     * mocked Event to play
     */
    private Event event2 = mockery.mock(Event.class, "Event2");
    
    /**
     * mocked exception handler
     */
    private IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
    
    /**
     * System under test
     */
    private AbstractInitiator abstractInitiator = new MockInitiator(moduleName,initiatorName, flow,exceptionHandler);
    
  
    /**
     * Constructor
     */
    public AbstractInitiatorTest(){
    	eventsToPlay.add(event1);
    	eventsToPlay.add(event2);
    }
    
    @Test
    public void testConstructor(){
        //just testing the constructor our system under test already used
        
        Assert.assertEquals("name should be that passed in on constructor", initiatorName, abstractInitiator.getName());
        Assert.assertEquals("moduleName should be that passed in on constructor", moduleName, abstractInitiator.getModuleName());
        Assert.assertEquals("flow should be that passed in on constructor", flow, abstractInitiator.getFlow());
    }
    
    @Test 
    public void testNotifyMonitorListeners_willNotifyAllRegisteredListeners(){
        final String runningState = InitiatorState.RUNNING.getName();
        //add two monitorListeners and check that they are notified with the state name
        mockery.checking(new Expectations()
        {
            {
                one(firstMonitorListener).notify(runningState);
                one(secondMonitorListener).notify(runningState);
            }
        });
        
        abstractInitiator.addListener(firstMonitorListener);
        abstractInitiator.addListener(secondMonitorListener);
        abstractInitiator.notifyMonitorListeners();
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void testAddRemoveNotifyListener()
    {
        
        
        //by default there should be no monitorListeners registered with this initiator
        Assert.assertTrue("by default there should be no monitorListeners registered with this initiator", abstractInitiator.getMonitorListeners().isEmpty());
        
        abstractInitiator.addListener(firstMonitorListener);
        abstractInitiator.addListener(secondMonitorListener);
        
        Assert.assertEquals("there should be exactly 2 monitor listeners, following the registrtion of two distinct listeners", 2, abstractInitiator.getMonitorListeners().size());        
        Assert.assertEquals("first MonitorListener should be the first one added", firstMonitorListener, abstractInitiator.getMonitorListeners().get(0));
        Assert.assertEquals("second MonitorListener should be the second one added", secondMonitorListener, abstractInitiator.getMonitorListeners().get(1));
        
        //remove one of the listeners and check that only the other one now is registered
        abstractInitiator.removeListener(firstMonitorListener);       

        Assert.assertEquals("there should be exactly 1 monitor listeners, after we started with two, and we deregistered 1", 1, abstractInitiator.getMonitorListeners().size());        
        Assert.assertFalse("list of registered monitor listeners should not contain the one we deregistered", abstractInitiator.getMonitorListeners().contains(firstMonitorListener));
    }
    
    @Test
    public void testGetState(){
        Assert.assertEquals("getState should return InitiatorState.RUNNING if initiator implementation isRunning(), but not isRecovering()", InitiatorState.RUNNING,abstractInitiator.getState()); 
        
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertEquals("getState should return InitiatorState.RECOVERING if initiator implementation isRunning(), AND isRecovering()", InitiatorState.RECOVERING,abstractInitiator.getState()); 
    
        ((MockInitiator)abstractInitiator).setRunning(false);
        Assert.assertEquals("getState should return InitiatorState.STOPPED if initiator implementation !isRunning(), AND !isError()", InitiatorState.STOPPED,abstractInitiator.getState()); 
    
        ((MockInitiator)abstractInitiator).setError(true);
        Assert.assertEquals("getState should return InitiatorState.ERROR if initiator implementation !isRunning(), AND isError()", InitiatorState.ERROR,abstractInitiator.getState()); 
    }
    
    @Test 
    public void testIsError(){
        Assert.assertFalse("isError() should return value of the error flag", abstractInitiator.isError()); 
        ((MockInitiator)abstractInitiator).setError(true);
        Assert.assertTrue("isError() should return value of the error flag", abstractInitiator.isError()); 

    }
    
    @Test
    public void testStart_willClearFlagsBeforeInvokingStartInitiator(){
        
        //set up the mock implementation such that the error flag and the stopping flag are set beforehand, - we just want to make sure these get cleared on start
        ((MockInitiator)abstractInitiator).setError(true);
        ((MockInitiator)abstractInitiator).setStopping(true);
        Assert.assertFalse("just checking that our mock implementation has not had startInitiator called on it before", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        abstractInitiator.start();
        Assert.assertTrue("startInitiator should have been called as a part of the start method", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
    }

    @Test
    public void testStop_onRecoveringInitiator_willSetStoppingFlagAndCancelRetryBeforeInvokingStopInitiator(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    }
   
    @Test
    public void testStop_onNonRecoveringInitiator_willSetStoppingBeforeInvokingStopInitiator(){
        ((MockInitiator)abstractInitiator).setRecovering(false);
        
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    }
    
    /**
     * When handleAction is passed a null action, and the initiator is recovering, it should invoke the completeRetry routine
     */
    @Test
    public void testHandleAction_withNullAction_willCompleteRetryIfRecovering(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
        
        //invoke the method that will result in handleAction
        ((MockInitiator)abstractInitiator).invokeHandleAction(null);
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
    }
    
    /**
     * Tests that invocation of invokeFlow with a null Event List will only result in completing any retry, nothing more
     */
    @Test
    public void testInvokeFlow_withNullEventList_willCompleteRetryIfRecovering(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeInvokeFlow(null);
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

    }
    
    /**
     * Tests that invocation of invokeFlow with an empty Event List will only result in completing any retry, nothing more
     */
    @Test
    public void testInvokeFlow_withEmptyEventList_willCompleteRetryIfRecovering(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeInvokeFlow(new ArrayList<Event>());
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
    }
    
    /**
     * Tests that invocation of invokeFlow with a 2 Event List will:
     * 	1) invoke the flow cleanly with each in turn
     *  2) not get an exception for either
     *  3) handle the null action scenario by completing any retry 
     */
    @Test
    public void testInvokeFlow_withTwoEventsResultingInNoActions_willCompleteRetryIfRecovering(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
        
        final Sequence sequence = mockery.sequence("invocationSequence"); 
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event1)));
                inSequence(sequence);
                will(returnValue(null));
                
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event2)));
                inSequence(sequence);
                will(returnValue(null));
            }
        });
        
        
        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeInvokeFlow(eventsToPlay);
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
    
        mockery.assertIsSatisfied();
    }
    
    /**
     * Tests that invocation of invokeFlow with a 2 Event List, where the first Event fails will
     * 	1) invoke the flow with the first Event only
     *  2) get an exceptionAction back for the firstEvent
     *  3) handle action
     */
    @Test
    public void testInvokeFlow_withTwoEventsFirstFailing_willHandleAction(){        
        final Sequence sequence = mockery.sequence("invocationSequence"); 
        final IkasanExceptionAction exceptionAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
        
        final Throwable throwable = new RuntimeException();
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event1)));
                inSequence(sequence);
                will(throwException(throwable));
                
                one(exceptionHandler).invoke(with(any(String.class)), (Event) with(equal(event1)), (Throwable) with(equal(throwable)));
                will(returnValue(exceptionAction));
                inSequence(sequence);
                
            }
        });
        
        
        //invoke the method that will result in invokeFlow being called
        AbortTransactionException abortTransactionException = null;
        try{
	        ((MockInitiator)abstractInitiator).invokeInvokeFlow(eventsToPlay);
	        Assert.fail();
        }catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        //check that the action was handled
        Assert.assertTrue("Initiator should now be stopping, if the exceptionAction was handled",abstractInitiator.isStopping());
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Tests that invocation of invokeFlow with a 2 Event List, where the second Event fails will
     * 	1) invoke the flow with the both Events
     *  2) get an exceptionAction back for the second event only
     *  3) handle action
     */
    @Test
    public void testInvokeFlow_withTwoEventsSecondFailing_willHandleAction(){        
        final Sequence sequence = mockery.sequence("invocationSequence"); 
        final IkasanExceptionAction exceptionAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
        
        final Throwable throwable = new RuntimeException();
        
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event1)));
                inSequence(sequence);
                will(returnValue(null));
                
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event2)));                
                inSequence(sequence);
                will(throwException(throwable));
                
                one(exceptionHandler).invoke(with(any(String.class)), (Event) with(equal(event2)), (Throwable) with(equal(throwable)));
                will(returnValue(exceptionAction));
                inSequence(sequence);
                
            }
        });
        
        
        //invoke the method that will result in invokeFlow being called
        AbortTransactionException abortTransactionException = null;
        try{
	        ((MockInitiator)abstractInitiator).invokeInvokeFlow(eventsToPlay);
	        Assert.fail();
        }catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        //check that the action was handled
        Assert.assertTrue("Initiator should now be stopping, if the exceptionAction was handled",abstractInitiator.isStopping());
        
        mockery.assertIsSatisfied();
    }
    
    class MockInitiator extends AbstractInitiator implements Initiator{
        
        private boolean running = true;
        private boolean recovering = false;
        private boolean startInitiatorCalled = false;
        private boolean completeRetryCycleCalled = false;

        
        public boolean isCompleteRetryCycleCalled()
        {
            return completeRetryCycleCalled;
        }


        public void invokeHandleAction(IkasanExceptionAction ikasanExceptionAction){
            handleAction(ikasanExceptionAction);
        }
        
        public void invokeInvokeFlow(List<Event>events){
            invokeFlow(events);
        }
        
        
        public boolean isStartInitiatorCalled()
        {
            return startInitiatorCalled;
        }



        public boolean isStopInitiatorCalled()
        {
            return stopInitiatorCalled;
        }

        private boolean stopInitiatorCalled = false;

        public MockInitiator(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler)
        {
            super(moduleName, name, flow, exceptionHandler);
        }

        public void setRunning(boolean running){
            this.running = running;
        }
        
        public void setRecovering(boolean recovering){
            this.recovering=recovering;
        }

        public String getType()
        {
            // TODO Auto-generated method stub
            return null;
        }


        
        public void setError(boolean error){
            this.error = error;
        }
        
        public void setStopping(boolean stopping){
            this.stopping = stopping;
        }

        public boolean isRecovering()
        {
            return recovering;
        }

        public boolean isRunning()
        {
            return running;
        }


        @Override
        protected void completeRetryCycle()
        {
            recovering=false;
            completeRetryCycleCalled = true;
        }

        @Override
        protected Logger getLogger()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void cancelRetryCycle()
        {
            recovering=false;
            
        }

        @Override
        protected void startInitiator() throws InitiatorOperationException
        {
            running = true;
            startInitiatorCalled=true;
            Assert.assertFalse("stopping flag should never be set by the time startInitiator is called", stopping);
            Assert.assertFalse("error flag should never be set by the time startInitiator is called", error);
            
        }

        @Override
        protected void stopInitiator() throws InitiatorOperationException
        {
            running = false;
            stopInitiatorCalled = true;
            Assert.assertTrue("stopping flag should always be set prior to stopInitiator being called", stopping);
            Assert.assertFalse("isRecovering() should never be true once stopInitiator is called", isRecovering());
        }

        @Override
        protected void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException
        {
            // TODO Auto-generated method stub
            
        }
        
    }
}
