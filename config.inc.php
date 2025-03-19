<?php

$i = 0;
$i++;
$cfg['Servers'][$i]['host'] = 'identity-mysql';
$cfg['Servers'][$i]['user'] = 'root';
$cfg['Servers'][$i]['password'] = 'identity_service';
$cfg['Servers'][$i]['auth_type'] = 'config';
$cfg['Servers'][$i]['AllowNoPassword'] = false;

$i++;
$cfg['Servers'][$i]['host'] = 'inventory-mysql';
$cfg['Servers'][$i]['user'] = 'root';
$cfg['Servers'][$i]['password'] = 'inventory_service';
$cfg['Servers'][$i]['auth_type'] = 'config';
$cfg['Servers'][$i]['AllowNoPassword'] = false;

?>