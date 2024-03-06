JAVAC = javac
JAVA = java
SRC = src
OUT = out
MAIN = com.aziflaj.uptdistsys.Downloader

build:
	@$(JAVAC) -cp $(SRC) -d $(OUT) $(SRC)/*java
	@echo "Build complete"

run-t: build
	$(JAVA) -cp $(OUT) $(MAIN) t $(url)

run-p: build
	$(JAVA) -cp $(OUT) $(MAIN) p $(url)

# Define the command to clean the project
clean:
	rm -f $(OUT)

all: build run
