[Return](professionals.md)
## Terminology ##
The terminology in G_String can be quite confusing at first, this is partially due to the fact that concepts are drawn from different fields, where the same terms can have different meanings, and partially due to the development history of the G_String project, where terminological conflicts were only recognized too late, to correct without major rewrites of the whole program.

This section, therefore, serves to bring some order into the chaos.
### Facet ###
The term 'facet' was originally coined by L.J. Cronbach, defined as: 'sources of variation'. These can be related to the object of assessment, e.g. 'candidate' (facet of differentiation), and include various aspects of classification or stratification (facets of stratification), and on the other hand, components of the assessment tools and method (facets of generalization). This can be expressed as 'c x p'

The simplest case is a math test, where each of say 30 candidates has to solve 20 problems, resulting in a total of 600 true/false scores. The sample sizes are 30 (candidates) and 20 (problems). But in a country wide math assessment it might be 250 schools (s), each with a somewhat different number of candidates (c), solving problems (p) in 4 fields (f) of mathematics, and the number of problems may vary across the fields of mathematics. It would be considered a '(c:s) x (p:f) design, i.e. candidates are nested in schools, and problems are nested in fields.

### Effect ###
In Brennan's control file ('Script') the term 'Effect' is used for the hierarchy of dependencies: s, c:s, f, p:f. When determining the ANOVA variance components are calculated, one has to not only consider these 'primary' effects, but also all possible cross effects (secondary). In the urGENOVA output, both are lumped together as 'Effects'. In G_String we have called the former 'nests' and all possible Brennan Effects 'configurations'. 

### Nest ###
Unfortunately, by that time the term 'Nest' in G_String was already occupied by a single object of a class 'Nest', a container defining the overall structure, parameters and variables, as well as supporting methods. In other words, the term 'Nest' implies a single, central, large, complex object, whereas 'nests' are simple strings consisting of characters designating specific facets, and ocasional colons (:) indicating a nesting dependency.