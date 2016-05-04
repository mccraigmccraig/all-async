# all-async
exploring async options

slides [here](https://github.com/mccraigmccraig/all-async/blob/master/all-async.pdf)

## running the code
```
lein dev
```

## connecting a repl
```
lein repl :connect localhost:7888
```

## reloading code (from a repl)
```
 (bounce.core/reload!)
```

## accessing the api examples
```
http://localhost:3000/api/callback
http://localhost:3000/api/monad-unreliable-log

```

etc... all the api routes are defined in `all-async.service.api`
