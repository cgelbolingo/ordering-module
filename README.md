Assumptions:
- Interfaces cannot be changed, therefore I cannot add any other functions in ChannelListener and OrderedMessageQueueProvider
- Users of the module know that QueueHolder.batchSendToOutputQueue() must ba called to run a pass and send messages from channels to output queue.
- Users of the module know that OrderedMessageQueueProvider must be instantiated with the same outputQueue as QueueHolder.
- Users of the module know that ChannelListener must be instantiated with the same channelQueues as QueueHolder.
- Prioritization based on transfer size is used.
- Priorities are declared by the calling function and provided when creating an instance of each class.
- OutputQueue size will be declared externally by calling function.

Testing: 
Tested via main class. Different testing cases are commented for reference. 
