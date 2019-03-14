package com.template.flows;

import com.template.contracts.TemplateContract;
import com.template.states.IOUState;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import net.corda.core.contracts.Command;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

@InitiatingFlow
@StartableByRPC
public class IOUFlow extends FlowLogic<Void> {
    private final Integer iouValue; // value of the IOU being issued
    private final Party otherParty; // IOU’s borrower (the node running the flow is the lender)

    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();

    public IOUFlow(Integer iouValue, Party otherParty) {
        this.iouValue = iouValue;
        this.otherParty = otherParty;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public Void call() throws FlowException {
        /*
         * Choosing a notary
         */
        
        // Every transaction requires a notary to prevent double-spends and serve as a timestamping authority
        // retrieve the notary identity from the network map.
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        /*
         * Building the transaction
         */
        
        // create the transaction components
        IOUState outputState = new IOUState(iouValue, getOurIdentity(), otherParty);
        Command command = new Command<>(new TemplateContract.Commands.Action(), getOurIdentity().getOwningKey());

        // create a transaction builder and add the components to it
        // a mutable transaction class to which we can add inputs, outputs, commands, 
        // and any other items the transaction needs
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState, TemplateContract.ID)
                .addCommand(command);

        /*
         * Signing the transaction
         */
        
        // Now that we have a valid transaction proposal, we need to sign it. Once the transaction is signed, no-one
        // will be able to modify the transaction without invalidating this signature. This effectively makes the
        // transaction immutable.
        SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Creating a session with the other party
        FlowSession otherPartySession = initiateFlow(otherParty);

        /*
         * Finalising the transaction
         */
        
        // We now have a valid signed transaction. All that’s left to do is to get the notary to sign it, have that
        // recorded locally and then send it to all the relevant parties. Once that happens the transaction will become
        // a permanent part of the ledger. 
        
        // FinalityFlow does all of this for lender
        // For borrower to receive the transaction they just need a flow that responds to the seller's.
        
        // We finalise the transaction and then send it to the counterparty.
        subFlow(new FinalityFlow(signedTx, otherPartySession));

        return null;
    }
}