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
echo "------------- BRANCH: reference/submodule/ecfeed.core -------------"
echo ""
cd reference/submodule/ecfeed.core
git ls-remote origin $1
cd ../../..

echo ""
echo "------------- BRANCH: ecfeed.junit -------------"
echo ""
git ls-remote origin $1
echo ""

echo "**********************************************************************************************************************************"
