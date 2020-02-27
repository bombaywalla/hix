# hix - An example front-end in Re-frame and CLJS

This repo contains an example front-end application.
Mainly for me to learn Re-frame.

This front-end is matched with the `hax` backend server.

The pair `hix` and `hax` form a simple application that allows a new
user to register for a service, and then allows users to login and
logout.

## Prequisites

Note that these notes are for MacOS and `emacs` (with `cider` installed).

1. Install node and sass

```shell
	brew install node
	brew install sass
```

2. Install shadow-cljs globally.

```shell
	npm install --global shadow-cljs`
```

3. Make sure you are running `hax` before starting `hix` otherwise
   `hix` will have nowhere to connect to.

## Installation

1. Clone this repo.

```shell
	git clone https://github.com/bombaywalla/hix.git`
```

2. Set up node packages.

```shell
npm install --save`
```

3. Run the `sass` processor to generate the CSS for `hix`.
```shell
	sass -I node_modules/bulma -I node_modules/bulma-pricingtable/dist/css \
		resources/sass/hix.sass resources/public/css/hix.css
```

## Starting the hix app

1. Make sure you are running `hax`.

2. Start `emacs` and visit the `deps.edn` file.

3. Fire up a cider repl
   1. `^U ^C M-J`
   2. Type in `shadow` when prompted.
   3. Edit the nrepl command to include `-A:dev` after `shadow-cljs`.
   4. Type the `ENTER` key.

4. In the cider repl buffer:
```clojure
	(shadow/watch :app)
```

5. Open up a web browser and go tp <http://localhost:3000>



## Credits

I took inspiration and code from
- <https://github.com/jacekschae/conduit>
- <https://github.com/lambdaclass/holiday_ping>

Please look at them as additional examples.

## License

Copyright © 2020 Dorab Patel

Distributed under the MIT License.
