[Return](Function.md)

## Grab and Drop ##
The GS_L graphical user interface for both [Analysis](../../../blob/main/workbench/GS_L/src/steps/AnaGroups.java) and [Synthesis](../../../blob/main/workbench/GS_L/src/steps/SynthGroups.java) uses two similar 'Grab and Drop' methods. The first to order the sequence of facets (method: 'orderFacets'), the second for setting up the nesting structures ('setNestingGroups').

The user orders and nests the facets entirely by picking up graphical objects using the mouse button in a first location, and dropping them in a second. Although programming these actions with JaveFX can be tricky, it is well documented in a useful [tutorial by Jakob Jencov](http://tutorials.jenkov.com/javafx/drag-and-drop.html).

'orderFacets' exhibits a single 'ListView' (lvFacets). Its items, or 'cells' are created in a 'cellFactory'. The cells respond to a series of mouse events, such as: 'onDragEntered', or 'onMouseClicked' with specific actions. These cells are linked to an '[observableList](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html)' containing the facets.

'setNestingGroups' exhibits two 'ListViews' side-by-side: 'lvNested', and 'lvCrossed'. 'lvNested' initially contains the nested facets and 'lvCrossed' the crossed facets. But 'lvCrossed' is intended for the crossed effects, that is, the nested facets are moved by 'Grab-and-Drop' over to 'lvCrossed' and dropped on the facet, they are nested in.
