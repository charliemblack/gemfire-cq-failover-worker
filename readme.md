# Not ready for use

cd scripts
startGemFire.bat
Run two `:CqWorker.main()`
if you want some data to be played in - run one `:DataLoader.main()`
kill one worker - the other worker should fall through and pick up the work. 
potentally restart the worker that was killed and that client should block.