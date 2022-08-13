# Learning MongoDB

```sh
docker run -p 27017:27017 --name local-mongo -v /Users/oriol.canalias/tmp/:/users/tmp -d mongo
docker exec -it local-mongo mongo 
docker exec -it local-mongo bash
```

To import file:
```
mongoimport -c megasena --type csv --drop --headerline megasena.csv
```

## Collection and crud operations

Execute queries on mongo, we need to get cli `mongo` and execute `db.<collection name>.<operation>`

### Search
- `db.megasena.count`: Count of objects in collection
- `db.megasena.find`: Find the firsts elements of the collection
- `db.megasena.findOne()`: Find the first element in the collection
- `db.megasena.find({"Concurso":73}).pretty()`: Find by `Concurso` is `73` and show in pretty format.
-  `db.megasena.find({"Ganhadores_Sena":5},{"Concurso":true})`: Find by `Ganhadores_Sena` is `5` and show only the field `Concurso` (value true means that this field will be showed) and the `_id`, that is showed by default.  
- `db.megasena.find({taxa:{$exists:true}})` find objects with a column `taxa`
- `db.megasena.find({"Data Sorteio":/2009/})` or `db.megasena.find({"Data Sorteio":{$regex:'2009'}})` find all objects where `2019` is part of `Data Sorteio`. It is the `like` instruction for mongo.
- `db.megasena.find({"Nome":/john/i})` or `db.megasena.find({"Nome":{$regex:'john', $options: 'i'}})` similar than before, to search as case insensitive. For search at the begin of the field, whe can user `^` before search text. For search at the end of the field, we can use `$` after search text.
- `db.megasena.distinct("Ganhadores_Sena")` returns a list with the different values for the field `Ganhadores_Sena`.

Others search operators:
- $gt, $gte: great-than, great-than or equals
- $lt, $lte: less-than, less-than or equals
- $ne: not equals
- $in , $nin:  exists, not exists in a list
- $all: Apply on list field, exists all elements of parameter list
- $not: Diferent than
- $elemMatch: Apply a filter for all elements in a list: `{$elemMatch: {$gte: 30, $lt: 40 }}`
- $size: Size of the field list

### Advanced search features

We can use logic operator to concat conditions, for example `and` or `or`:
```
db.megasena.find({ $or:
   [ {"Ganhadores_Sena":{ $eq:5 } },
     {"Ganhadores_Sena":{ $eq:7 } }]
    })
```

**Sort**: To sort results, we can use the key `.sort(<field>)` after find instruction:

- `db.megasena.find({}).sort({nome: 1})`

The value 1 or -1 indicates ascendent or descendent order.

**Limit**: We can limit the number of elements:

- `db.megasena.find({}).limit(5)`

**Skip**: We can ignore some elements from the find result:

- `db.megasena.find({}).limit(5).skip(2)`

**Aggregation**: We can do `group by` using aggregation framework:

```
db.megasena.aggregate( { $group :
 { _id: null,
   soma:{$sum:"$Ganhadores_Sena"}
 }})
```

field `_id` is mandatory, so we can use to aggregate by field:

```
db.megasena.aggregate({ $group: {
        _id: "$Acumulado",
        soma: { $sum: "$Ganhadores_Sena" },
        count: { $sum: 1 }
    } })
```

We can, also, filter the results:

```
db.megasena.aggregate({ $group: {
        _id: "$Acumulado",
        soma: { $sum: "$Ganhadores_Sena" },
        count: { $sum: 1 }
    } },
    { $match: {_id: {$nin: ["", null]}}}
    )
```

We can, also, aggregate for more than one field, and sort the result:
```
db.megasena.aggregate({ $group: {
        _id: { ganhadores_sena:"$Ganhadores_Sena",
         acumulado:  "$Acumulado" },
        soma: { $sum: "$Ganhadores_Sena" },
        count: { $sum: 1 }
    } },
    { $match: {"_id.acumulado": {$nin: ["", null]}}},
    { $sort: {soma: 1}}
    )
```

**execution plan**:

### Create
- `db.megasena.insert({ "Concurso" : 99999,  "Ganhadores_Sena" : 0})` to insert a new object.

### Update
- `db.megasena.update({"Concurso" :99999 }, {"Nome":"John Smith"})` find all objects by `Concurso` is `99999` and update/add column `Nome` with value `John Smith`  only on the first object. The other columns will be deleted because we don't specify them.
- `db.megasena.update({"Concurso" :99999 }, { $set: {"Nome":"John Smith"}})` find all objects by `Concurso` is `99999` and update/add column `Nome` with value `John Smith` only on the first object. The other columns remain intouchble
- `db.megasena.update({"Concurso" :99999 }, { $set: {"Nome":"John Smith"}}, {multi: true})` do the same of before but applying for all match objects, not only the first.
- `db.megasena.update({"Concurso" :99999 }, { $set: {"Nome":"John Smith"}}, {multi: true, upsert:true})` the same of previous, but if doesn't find any object, will be create.

Other options to use like `$set` are:
- $unset: To remove a field from the object.
- $rename: to change the name of a field for the objects.


### Remove

- `db.megasena.remove({"Concurso": 99999})` remove all objects where `Concurso` is `99999`.
- `db.megasena.drop()` removes the collection.


### Validations
We can add validations for collections:
```
db.runCommand({
 collMod:"megasena",
 validator: {
 "Concurso":{$exists:true}
 }})
```

We can use anothers things to validate, like regular expressions using `$regex` instruction, or type of content using `$type: "string"`. See more here: https://docs.mongodb.com/manual/reference/operator/query/type/#available-types


### Capped collections
We can create a limited collection, by size and by number of elements, using as a kind of cache:
 ```
 db.createCollection("cachingCollection",
      { capped: true, size: 4096,
        max: 2 })
 ```
We can change an existing "normal" collection to a capped:
```
db.runCommand({convertToCapped:'normalCollection',
    size:8192})
```

Note: We cannot remove registers from a capped collection.

## Performance
We can get information about collections with the next instruction:
- `db.getCollection('megasena').stats()`: Returns information like indexes and size

To get information about a query, we can add the `.explain` at the end to see important information, like existing execution plans, winning execution plan, total docs and keys examined, returned docs, and so on.
- `db.getCollection('megasena').find({"ganhadores_sena": "5"}).explain('executionStats')`

An stage with `COLLSCAN` is bad, because we will need to read all documents on collection. This is an 

### Creating index

To improve performance, we can create indexs:
```
db.collection.createIndex(
    {<field1> : <order>,
    <field2> : <order> }
)
```

This approach can be used to create index for complex types (`embedded document`);

We can create, also, unique indexes, adding `{unique: true}` after field list.
```
db.megasena.createIndex({"Data Sorteio": 1}, {unique : true });
```

When we create an index, we block all read/write ops, but if we add option `{background: true}` we can avoid these blocks.

### TTL
We can configure our collection to expire documents based on date. To do this, we create an index with the next format:
```
db.collection.createIndex(
 { <field> : 1 },
 { expireAfterSeconds: <TimeToLiveInSeconds> }
);
```

### Partial index
Some index can grow with the collection size, and we can have problems of disk space. A solution can be an index using a expression:
```
db.collection.createIndex(
 { <campo1> : <ordem>,
   <campo2> : <ordem>,
   ...},
 { partialFilterExpression: { <expressão> } }   
);
```
For example:
- `db.megasena.createIndex({ "Acumulado" : 1}, { partialFilterExpression: { "Acumulado": {$eq: "SIM"} } })`   
);`

This can be useful when we want consult only with this field has some especific value. 

### Full text search
A full text search is the option to search not only for a part of the text, but also for similar parts.
```
db.collection.createIndex({<field>: "text"},
  {default_language: <language>} );
```

To search:
- `db.searchtext.find($text: {$search: "work"}})`: find parts of the text with word `working` or similar (`work` or `working`)
- `db.searchtext.find($text: {$search: "work -home"}})`: same of before, but the text doesn't have the word `home`.

We can create complex index with this, one text field and other with normal "index":
```
db.collection.createIndex({<normal_field>: 1, <text_field>: "text"},
  {default_language: <language>} );
```

## Administrator

### Storage
`WiredTiger` is the storage used by Mongo. Before WiredTiger, mongo used `NMAPv1`, but today is not used anymore.

### Generic commands
- `show dbs` : simple list with the databases on mongo.
- `db.adminCommand( { listDatabases: 1 } )`: Json list with databases, like the previous commnand but with more information
- `use <database name>` to select the current database
- `db.getCollectionNames()` get the list of collections in this database.
- `Object.bsonsize(db.megasena.findOne())` get size of the element

### Stored scripts
We can save and use `js` scripts on Mongo like stored procedures, but it is not recommended

### Resources
MongoDB uses the free memory to improve performance automatically. This is good, because you don't need an admin to do fine tunning, but for this reason is a good practice running MongoDB in isolated server.

### Security
Mongo has support to users and roles.

Create an admin user:
```
db.createUser({user: "admin",
                pwd: "admin",
             roles:[ "userAdminAnyDatabase",
                     "dbAdminAnyDatabase",
                     "readWriteAnyDatabase"]})
```
To enable this config, we need to add on `/etc/mongodb.conf`.

For docker: 
```
docker run -p 27017:27017 --name local-mongo  -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=admin -d mongo
```

Create a read-only user for a database:
```
> use <my-database>
> db.createUser(
    {
      user: "<username>",
      pwd: "<pass>",
      roles: [
         { role: "readWrite", db: "<my-database>" }
      ]
    }
)
```

### backup

We can do backup, got all mongo databases, for an unique database, or an only collection for a database.
- `mongodump`
- `mongodump --db <database name>`
- `mongodump --db <database name> --collection <collection name>`

Also, we can export as json a data from a collection:
- `mongoextort -d <database name> -c <collection name> --out <filename>.json`

To restore a backup, we can stop mongo frist and do it:
- `mongorestore --dbpath <data directory of mongo> dump` to restore all data
- `mongorestore --dbpath <data directory of mongo> --db <database name> dump/<database name>` to restore only a database, merging data if the database already exists.
- `mongorestore --drop --dbpath <data directory of mongo> --db <database name> dump/<database name>` to restore only a database, droping before existing data if the database already exists.

### Manage running operations
Sometime when may need to identify some slow queries and kill it. We can use the next commands:
- `db.currentOp()`. We can pass `true` value inside parantheses to get more details of current operations.
- `db.killOp(<opid>)`

If we have a big volume of conections, can be hard to identify slow queries. For this we can use something like this:
```
db.currentOp().inprog.forEach(
   function(op) {
     if(op.secs_running > 5) printjson(op);
   }
 )
```


## Clusters

### Replica set
Mongo can handle up to 12 nodes (minimum recomendation of 3 nodes). When the main node dies, one of the rest get the main position.

### Sharding
When our data is too big to let in a single server, we can apply a sharding strategy, dividing data enter servers. We need a couple of services:
- Shards: Mongo instances that contains partial data. Can be replica set.
- Config servers: Server that has the metadata about all architecture
- Query routing instances: Our application will be connect on it, and it redirect reads and writes to shards.



## Atlas
Every connection needs 1Mb on Ram

### Oplog
The oplog (operations log) is a special capped collection that keeps a rolling record of all operations that modify the data stored in your databases.

MongoDB only removes an oplog entry if:
- The oplog has reached the maximum configured size, and
- The oplog entry is older than the configured number of hours.

How it works: 
- The operation is applied on collection in primary node.
- The operation is registred on oplog in primary node.
- The oplog is copied to other nodes. (depends on `majority` configuration, this copy can be sync or async)
- The secundary node replies the operation on collection

### Indexes 

Index must to be follow the ESR Rule - Equality - Sort - Range
 -https://www.mongodb.com/blog/post/performance-best-practices-indexing
Perhaps is estrange, but the reason is if we leave sort for the last step, this sort will be happen in memory.

### Log analize
https://github.com/simagix/keyhole

### Script to get connections

```
db.currentOp(true).inprog.reduce(
    (accumulator, connection) => {
        ipaddress = connection.client ? connection.client.split(":")[0] : "Internal";
        accumulator[ipaddress] = (accumulator[ipaddress] || 0) + 1;
        accumulator["TOTAL_CONNECTION_COUNT"]++;
        return accumulator;
    },
    { TOTAL_CONNECTION_COUNT: 0 }
    )
```


## Connect remotely com docker
```
docker run -it  --rm mongo mongosh "mongodb+srv://cluster0.3ru6q.mongodb.net/appcustomer" --username root
```