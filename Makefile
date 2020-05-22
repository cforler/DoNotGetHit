CC=javac
DIR=dngh

all: DoNotGetHit DoNotGetHit.jar run

DoNotGetHit:
	$(CC) $(DIR)/*.java


DoNotGetHit.jar:
	jar cfmv $@ Manifest $(DIR)/*.class


run: DoNotGetHit.jar
	java  -jar DoNotGetHit.jar

clean: 
	$(RM) $(DIR)/*~  $(DIR)/*.class DoNotGetHit.jar
