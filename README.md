# IOUd
**I O(we) Y(ou) d(e)**

## Introduction

This test example is going to demonstrate a simple CorDapp that will allow nodes on the blockchain to agree new IOUs with each other, as long as they obey the following contract rules:

- The IOU’s value is strictly positive
- A node is not trying to issue an IOU to itself

We will deploy and run the CorDapp on four test nodes:

Notary (runs a notary service)
PartyA
PartyB
PartyC

Because data is only propagated on a need-to-know basis, any IOUs agreed between PartyA and PartyB become “shared facts” between PartyA and PartyB only. PartyC won’t be aware of these IOUs.

All communication between nodes is *point-to-point* (data is shared only on a **need-to-know** basis).

Each network has a **network map service** that maps each well-known **node identity** (represents the node in transactions) to an **IP address**. These IP addresses are used for messaging between nodes.

## Design

CorDapps usually define at least three things:

- States - the (possibly shared) facts that are written to the ledger
- Flows - the procedures for carrying out specific ledger updates
- Contracts - the constraints governing how states of a given type can evolve over time

IOUd CorDapp will define the following components:

### State

State will be the **IOUState**, representing an IOU. It will contain the IOU’s value, its lender and its borrower. We can visualize IOUState as follows:

                        IOU_STATE

            PARTICIPANTS            PROPERTIES
            Alice                   __Lender:__ Alice
            Bob                     **Borrower:** Bob
                                    **Amount:** 10


### Flow

Flow will be the **IOUFlow**. This flow will completely automate the process of issuing a new IOU onto a ledger. It has the following steps:

**INITIATOR** CREATE TX -> SIGN TX -> RECORD TX -> SEND (TX + BORRWER SIG)  ->               END

**ACCEPTOR**                                                                -> RECORD TX  -> END

### Contract

Default **TemplateContract** will be used. We will update it to create a fully-fledged IOUContract in the next tutorial.



            
