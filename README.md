# DICOM-Metadata-Extract-Modifier

> Proyecto Java simple que utiliza dcm4che para leer y editar los metadatos de imágenes DICOM 

Los metadatos de las imágenes DICOM se componen de 3 campos:

> Tag Address: Valor hexadecimal, identificador
> 
> VR: Identificador del valor, o del tipo del mismo
> 
> Value: Valor del metadato.

La librería dcm4che proporciona una tabla para acceder a metadatos específicos, mediante la conversión de Tag Address a un número decimal. (Es un ENUM de números enteros)

Se puede obtener una lista de todos los valores disponibles desde el siguiente enlace:

https://www.dicomlibrary.com/dicom/dicom-tags/

El proyecto cuenta de una clase Launcher en la que se implementan varios métodos como ejemplo para implementar distintas funcionalidades:

> readMetadata

Lee y devuelve un Array con todos los metadatos de una imágen DICOM.

> modifyMetadata

Método que recibe como input una imágen DICOM y un array de atributos a modificar.

Para modificar un atributo, hace falta su Tag Address y su VR.

El método modificará los atributos indicados y los guardará en un duplicado de la imágen original, también indicado por parámetros.

> readTagFromDICOMImage

Método que lee el valor de una metadato específico de una imágen DICOM