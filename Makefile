.PHONY: run

build:
	docker build . -t chanty

run:
	docker run -it --rm -v "$(shell pwd)":/usr/src/app -v "$(shell pwd)/.m2:/root/.m2"　\
     -p 3030:3030 chanty

repl:
	docker run -it --rm -v "$(shell pwd)":/usr/src/app -v "$(shell pwd)/.m2:/root/.m2" \
     -p 3030:3030 -p 40000:40000 chanty lein repl :start :port 40000
