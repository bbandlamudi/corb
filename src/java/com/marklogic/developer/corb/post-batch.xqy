xquery version "1.0-ml";

 declare variable $URIS_MODULE_METADATA as xs:string external;
 
 let $_ := xdmp:log("post-batch: URIS_MODULE_METADATA: " || $URIS_MODULE_METADATA)
 return 'success'