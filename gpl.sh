#!/bin/bash

echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
echo "**********************************************************************************************************************************"

echo ""
echo "------------- PULLING: reference/submodule/ecfeed.core -------------"
echo ""
cd reference/submodule/ecfeed.core
git pull origin $1
cd ../../..

echo ""
echo "------------- PULLING: ecfeed.junit -------------"
echo ""
git pull origin $1
echo ""

echo "**********************************************************************************************************************************"
