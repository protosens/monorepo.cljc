#! /bin/bash


set -e
bb clojure -X:module/playwright.test.cljs firefox
