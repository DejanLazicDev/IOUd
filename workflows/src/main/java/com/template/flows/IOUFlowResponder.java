package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.ReceiveFinalityFlow;

// The flow is annotated with InitiatedBy(IOUFlow.class), 
// which means that your node will invoke IOUFlowResponder.call 
//when it receives a message from a instance of Initiator running on another node. 
// This message will be the finalised transaction which will be recorded in the borrower’s vault.
@InitiatedBy(IOUFlow.class)
public class IOUFlowResponder extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public IOUFlowResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        /*
         * Creating the borrower’s flow
         */

        // The borrower has to use ReceiveFinalityFlow in order to receive and record the transaction;
        // it needs to respond to the lender’s flow.
        subFlow(new ReceiveFinalityFlow(otherPartySession));

        return null;
    }
}