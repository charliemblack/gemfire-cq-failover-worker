# Not ready for use

1) copy `gemfire-distributed-types-0.1.0.jar` to the project\libs dir
2) cd scripts
3) startGemFire.bat
4) Run two `:CqWorker.main()`
5) if you want some data to be played in - run one `:DataLoader.main()`
6) kill one worker - the other worker should fall through and pick up the work. 
7) potentally restart the worker that was killed and that client should block.
