Documentation
=====

UML Diagrams
-----

The files with `.uml.txt` extension are written in [http://plantuml.com/graphviz-dot PlantUML]
DSL.  You can generate PNG diagrams from them using the Plant UML software.  You will
also need to install GraphViz tools (especially the `dot` executable) on your system, as
PlantUML uses it for class diagrams.  If the `dot` executable is not at `/usr/bin/dot`
or `/usr/local/bin/dot`, then set the environment variable `GRAPHVIZ_DOT` to its
exact path (the full path to the exutable, not just the containing directory).

Diagrams
'''''

- `java_lang_annotation.uml.txt`: relevant contents of the `java.lang.annotation` package
  that are used heavily in our annotation processor.
  
