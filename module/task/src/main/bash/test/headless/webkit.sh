#! /bin/bash


set -e
clj -X:module/playwright.test.cljs webkit
