[Return](professionals.md)
## Preprocessing Score Data ##
Analysis is performed on score data presented in a data file. Data need to be properly formatted, normalized, and adjusted for missing data points, before they can be handed to 'urGENOVA' for processing. This happens in ['Filer'](../../../blob/main/workbench/GS_L/src/utilities/Filer.java). The raw data file is read by 'readDataFileNew', and then normalized and written to the processed file in 'writeDataFileNew'.

G_String accepts integer scores organized in rows. The individual scores have to be separated (consistently), either by commas, tabs or spaces. Separation by spaces is discouraged, because it does not allow detection of missing data points.

If 'readDataFileNew' detects a missing data point, it replaces it by the letter 'x'. It also calculates the grand means of all proper integer numbers. Method 'writeDataFileNew' then generates a consistent file of Double numbers, maintaining the tabbed row/column organization, with values 0.0 replacing the letter 'x' and integer score iScore replaced by a Double number: iScore - grand means.