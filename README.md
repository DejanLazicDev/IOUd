# IOUd
## I O(we) Y(ou) d(e)

The example CorDapp allows nodes to agree IOUs with each other, as long as they obey the following contract rules:

- The IOU’s value is strictly positive
- A node is not trying to issue an IOU to itself

We will deploy and run the CorDapp on four test nodes:

Notary, which runs a notary service
PartyA
PartyB
PartyC

Because data is only propagated on a need-to-know basis, any IOUs agreed between PartyA and PartyB become “shared facts” between PartyA and PartyB only. PartyC won’t be aware of these IOUs.


All communication between nodes is *point-to-point* (data is shared only on a **need-to-know** basis).

Each network has a **network map service** that maps each well-known **node identity** (represents the node in transactions) to an **IP address**. These IP addresses are used for messaging between nodes.
