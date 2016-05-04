# all-async
exploring async options

slides [here](https://github.com/mccraigmccraig/all-async/blob/master/all-async.pdf)

## running the code
```
lein dev
```
which starts an API server on `localhost:3000`

## connecting a repl
```
lein repl :connect localhost:7888
```

## reloading code (from a repl)
```
 (bounce.core/reload!)
```

## accessing the API examples
```
http://localhost:3000/api/callback
http://localhost:3000/api/monad-unreliable-log

```
etc... all the api routes are defined in `all-async.service.api`
