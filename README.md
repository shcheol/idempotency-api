# idempotency-api

[![](https://jitpack.io/v/shcheol/idempotency-api.svg)](https://jitpack.io/#shcheol/idempotency-api)

## Introduce
___

This library supports for converting non-idempotent http methods POST, PATCH to idempotent.


## Features
___
- Convert non-idempotent http methods to idempotent.

## Caution
___
- idempotency-key
  - The key is sufficiently random and must be generated as a unique value.
  - ex) UUID
- idempotency-key store
  - DB or cache server should be used to support idempotent in a distributed environment
  - REDIS, MySQL ..


## Errors
___

| Error Code               | Scenario                                                                                 |
|--------------------------|------------------------------------------------------------------------------------------|
| 400 Bad Request          | call idempotent-request without idempotency-key or invalid Key                           |
| 409 Conflict             | call new-request with same idempotency-key while previous request is proccessing         
| 422 Unprocessable Entity | retryed request with same idempotency-key has differenct payload with the previous thing 




## How To Use
___

Step1: Add it in your root build.gradle at the end of repositories:
```
    dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

```
Step2: Add the dependency
```
dependencies {
    implementation 'com.github.shcheol:idempotency-api:Tag'
}
```

Step3: implements the IdempotencyKeyStore
```
public XxxIdempotencyKeyStore implements IdempotencyKeyStore {

        @Override boolean has(String key){ ... }
	@Override void set(String key, Object value){ ... }
	@Override Object get(String key){ ... }
	@Override void remove(String key){ ... }
}

public interface IdempotencyKeyStore {

	boolean has(String key);
	void set(String key, Object value);
	Object get(String key);
	void remove(String key);
}
```

Step4: Overriding the @IdempotencyApi Annotation

this works only non-idempotent http methods(post, patch)
```
    @IdempotencyApi(storeType="xxxIdempotencyKeyStore")
    @PostMapping("/test")
    public String method(){
        do Somthing ...
        return ...
    }
```

Step5: Put idempotency-key on header, Call idempotency-api   
```
curl --request POST \
  --url https://localhost:8080/test \
  --header 'Content-Type: application/json' \
  --header 'idempotent: UUID Random String..' \
```