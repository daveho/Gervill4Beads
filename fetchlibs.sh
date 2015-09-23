#! /bin/bash

# Script to download required jarfiles.
# Since Gervill is built into JDK7 and JDK8, we don't need
# to download it.  So, just download Beads.

jars='beads-io.jar beads.jar jarjar-1.0.jar jl1.0.1.jar jna.jar mp3spi1.9.4.jar org-jaudiolibs-audioservers-jack.jar org-jaudiolibs-audioservers-javasound.jar org-jaudiolibs-audioservers.jar org-jaudiolibs-jnajack.jar tools.jar tritonus_aos-0.3.6.jar tritonus_share.jar'

mkdir -p lib

echo -n "Extracting Beads jar files..."
for j in $jars; do
	if [ ! -e "lib/$j" ]; then
		unzip -p Beads.zip beads/library/$j > lib/$j
		echo -n "."
	fi
done
echo "done"
