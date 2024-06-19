# Example: bulkloading data in a Virtuoso docker image

_Copyright (C) 2022 OpenLink Software <support@openlinksw.com>_

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Introduction](#introduction)
- [Downloading and running the example](#downloading-and-running-the-example)
- [Explanation](#explanation)
  - [The docker-compose.yml script](#the-docker-composeyml-script)
  - [The ./data directory](#the-data-directory)
    - [Notes on the bulkloader](#notes-on-the-bulkloader)
  - [The ./scripts directory](#the-scripts-directory)
    - [The 10-bulkload.sql script](#the-10-bulkloadsql-script)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction

Both the 
[OpenLink Virtuoso Commercial docker image](https://hub.docker.com/repository/docker/openlink/virtuoso-closedsource-8)
(openlink/virtuoso-commercial-8) and the 
[OpenLink Virtuoso Open Source docker image](https://hub.docker.com/repository/docker/openlink/virtuoso-opensource-7)
(openlink/virtuoso-opensource-7) allow users to run a combination of shell scripts and SQL
scripts to initialize a new database.

This example shows how a Virtuoso Open Source Docker instance, started by docker-compose,
can bulkload initial data into the QUAD store when initializing a new database.

It has been tested on both Ubuntu 18.04 (x86_64) and macOS Big Sur 11.6 (x86_64 and Apple Silicon).

Most modern Linux distributions provide docker packages as part of their repository.

For Apple macOS and Microsoft Windows Docker installers can be downloaded from the 
[Docker website](https://docker.com/products).

**Note**: Installing software like git, Docker and Docker Compose is left as an excercise for
the reader.


##  Downloading and running the example

The source code for this example can be cloned from its repository on GitHub using the following
command:

```shell
$ git clone https://github.com/openlink/vos-docker-bulkload-example
```

The example is started using the following commands:

```shell
$ cd vos-docker-bulkload-example
$ docker-compose pull
$ docker-compose up
```

Once Virtuoso has started, you can use a browser to connect to the local SPARQL endpoint:

```text
http://localhost:8890/sparql
```

Cut and paste the following query and press the 'Execute' button.

```text
SELECT * from <urn:bulkload:test1> WHERE { ?s ?p ?o }
```

which should give the following result:

| s   | p   | o                                |
| --- | --- | -------------------------------- |
| s1  | p1  | This is example 1 (uncompressed) |
| s3  | p3  | This is example 3 (gzip)         |
| s4  | p4  | This is example 4 (xz)           |


Next, cut and paste the following query and press the 'Execute' button.

```text
SELECT * from <urn:bulkload:test2> WHERE { ?s ?p ?o }
```

which should give the following result:

| s   | p   | o                         |
| --- | --- | ------------------------- |
| s2  | p2  | This is example 2 (bzip2) |


To stop the example, just press `CTRL-C` on the docker-compose window.

Finally to clean the example, run the following command:
```shell
$ docker-compose rm
```


## Explanation


### The docker-compose.yml script

This is the `docker-compose.yml` script we are using in this example:
```yml
version: "3.3"
services:
  virtuoso_db:
    image: openlink/virtuoso-opensource-7
    volumes:
      - ./data:/database/data
      - ./scripts:/opt/virtuoso-opensource/initdb.d
    environment:
      - DBA_PASSWORD=dba
    ports:
      - "1111:1111"
      - "8890:8890"
```

The docker compose program uses this information to run the following steps:

  1. Downloads the `openlink/virtuoso-opensource-7` docker image from Docker Hub if it does not
find a version of the image in your local docker cache. As you may already have an older image in
your cache, you may want to first run `docker-compose pull` to make sure you have downloaded
the absolute latest version of the image.
  2. Creates an instance of this docker image.
  3. Mounts the local `data` directory on the host os, as `/database/data` in the docker instance.
  4. Mounts the local `scripts` directory on the host OS, as `/initdb.d` in the docker instance.
  5. Sets the `dba` password to something trivial.
  6. Exposes the standard network ports `1111` and `8890`.
  7. Runs the registered startup script for this image.


### The ./data directory

This directory contains the initial data that we want to load into the database. 

```
./data
├── README.md
├── example1.nt
├── example2.nt.bz2
├── example2.nt.graph
├── example3.nt.gz
├── global.graph
└── subdir
    └── example4.nt.xz
```
The `docker-compose.yml` script mounts this data directory below the `/database` directory as
`/database/data`.

Since the database directory is in the `DirsAllowed` setting in the `[Parameters]` section,
we do not have to make modifications to the `virtuoso.ini` configuration file.


#### Notes on the bulkloader
The Virtuoso bulkloader can automatically handle compressed (.gz, .bz2 and .xz) files and will
choose the appropriate decompression function to read the content from the file.

It then chooses an appropriate parser for the data based on the suffix of the file:

  * **N-QUAD** when using the .nq or .n4 suffix
  * **Trig** when using the .trig suffix
  * **RDF/XML** when using .xml, .owl, .rdf or .rdfs suffix
  * **Turtle** when using .ttl or .nt suffix

While the function <code>ld_dir_all()</code> allows the operator to provide a graph name, it is much simpler
for the data directory to contain hints on the graph names to use, especially when there are
a number of files that needed to be loaded either in the same graph or in different graphs.

In this example the file `example2.nt.gz` needs to be loaded in a different graph than the other
two data files, so we create a `example2.nt.graph` file which contains the graph name for this
data file. Note that although the datafile has the `.gz` extension, the graph file does not.

The other two data files in this directory, `example1.nt` and `example3.nt.gz` do not have
their own graph hint file. In this case the bulkloader sees there is a `global.graph` file in
the same directory and uses its contents for these two files.

Finally the `example4.nt.xz` also uses the information from `global.graph` as subdirectories
automatically inherit the graph name from their parent directory.

If the `global.graph` file is not present, the graph argument of the <code>ld_dir_all()</code>
function is used.




### The ./scripts directory

This directory can contain a mix of shell (.sh) scripts and Virtuso PL (.sql) scripts that can
perform functions such as:

  * Installing additional Ubuntu packages.
  * Loading data from remote locations such as Amazon S3 buckets, Google Drive, or other locations.
  * Bulkloading data into the virtuoso database.
  * Installing additional `VAD` packages into the database.
  * Adding new virtuoso users.
  * Granting permissions to virtuoso users.
  * Regenerate freetext indexes or other initial data.

The scripts are run only once during the initial database creation; subsequent restarts of the
docker image will not cause these script to be rerun.

The scripts are run in alphabetical order, so we suggest starting the script name with a sequence
number, so the ordering is explicit.

For security purposes, Virtuoso will run the `.sql` scripts in a special mode and will not
respond to connections on its SQL (1111) and/or HTTP (8890) ports.

At the end of each `.sql` script, Virtuoso automatically performs a `checkpoint` to make sure
the changes are fully written back to the database. This is very important for scripts
that use the bulkloader function `rdf_loader_run()` or for any reason manually change the ACID
mode of the database.

After all the initialization scripts have run to completion, Virtuoso will be started normally
and start listening to requests on its SQL (1111) and HTTP (8890) ports.


#### The 10-bulkload.sql script


```SQL
--
--  Copyright (C) 2022 OpenLink Software
--

--
--  Add all files that end in .nt
--
ld_dir_all ('data', '*.nt', 'no-graph-1')
;

--
--  Add all files that end in .bz2, .gz or .xz to show that the Virtuoso bulkloader 
--  can load compressed files without manual decompression
--
ld_dir_all ('data', '*.bz2', 'no-graph-3')
;

ld_dir_all ('data', '*.gz', 'no-graph-2')
;

ld_dir_all ('data', '*.xz', 'no-graph-4')
;

--
--  Now load all of the files found above into the database
--
rdf_loader_run()
;

--
--  End of script
--
```
