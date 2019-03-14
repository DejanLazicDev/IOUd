# IOUd
**I O(we) Y(ou) d(e)**

## Introduction

This test example is going to demonstrate a simple CorDapp that will allow nodes on the blockchain to agree new IOUs with each other, as long as they obey the following contract rules:

- The IOU's value is strictly positive
- A node is not trying to issue an IOU to itself

We will deploy and run the CorDapp on four test nodes:

Notary (runs a notary service)
PartyA
PartyB
PartyC

Because data is only propagated on a need-to-know basis, any IOUs agreed between PartyA and PartyB become "shared facts" between PartyA and PartyB only. PartyC won't be aware of these IOUs.

All communication between nodes is *point-to-point* (data is shared only on a **need-to-know** basis).

Each network has a **network map service** that maps each well-known **node identity** (represents the node in transactions) to an **IP address**. These IP addresses are used for messaging between nodes.

## Design

CorDapps usually define at least three things:

- **States** - (possibly shared) *facts* that are written to the ledger
- **Flows** - *procedures* for carrying out specific ledger updates
- **Contracts** - *constraints* governing how states of a given type can evolve over time

IOUd CorDapp will define the following components:

### State

In Corda, *shared facts* on the blockchain are represented as **states**. A State consists of a *list of the entities* for which this state is relevant (ContractState interface implementation).

Besides, our State will also need properties to track the relevant features of the IOU:

- **Value** of the IOU
- **Lender** of the IOU
- **Borrower** of the IOU

#### Implementation

State will be **IOUState**, representing an IOU. It will contain the IOU's value, its lender and its borrower:

                        IOU_STATE

            PARTICIPANTS            PROPERTIES
            Alice                   Lender: Alice
            Bob                     Borrower: Bob
                                    Amount: 10

### Flow

A **flow** encodes a *sequence of steps* that a node can perform to achieve a specific *ledger update*. By installing new flows on a node, we allow the node to handle new business processes. 

The flow we define will allow a node to issue a State onto the ledger.

#### Implementation

The goal of our flow will be to *orchestrate an IOU issuance transaction*. **Transactions** in Corda are the *atomic* units of change that *update the ledger*. Each transaction is a proposal to *mark* zero or more *existing* states as **historic** (**inputs**), while *creating* zero or more *new states* (**outputs**).

Flow will be **IOUFlow**. This flow will completely automate the process of issuing a new IOU onto a ledger. It has the following steps:

<pre>
INITIATOR:  CREATE TX   ->  SIGN TX   ->  RECORD TX   ->  SEND (TX + BORRWER SIG) ->                END

ACCEPTOR:                                                                         -> RECORD TX  ->  END
</pre>

The goal of our flow will be to orchestrate an IOU issuance transaction. Transactions in Corda are the atomic units of change that update the ledger. Each transaction is a proposal to mark zero or more existing states as historic (the inputs), while creating zero or more new states (the outputs).

The process of creating and applying this transaction to a ledger will be conducted by the IOU’s lender, and will require the following steps:

Building the transaction proposal for the issuance of a new IOU onto a ledger
1. Signing the transaction proposal
2. Recording the transaction and sending it to the IOU’s borrower so that they can record it too
3. We also need the borrower to receive the transaction and record it for itself. At this stage, we do not require the borrower to approve and sign IOU issuance transactions. 

### Contract

Default **TemplateContract** will be used. We will update it to create a fully-fledged IOUContract in the next tutorial.

