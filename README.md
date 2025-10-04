Assumptions:
- Interfaces cannot be changed, therefore I cannot add any other functions in ChannelListener and OrderedMessageQueueProvider
- Users of the module know that QueueHolder.batchSendToOutputQueue() must ba called to run a pass and send messages from channels to output queue.
- Prioritization based on transfer size is used.
- Priorities are declared by the calling function and provided when creating an instance of each class.
