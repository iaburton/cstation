# cstation

Package cstation is a Go package designed to be used with [gomobile](https://godoc.org/golang.org/x/mobile/cmd/gomobile) and acts as the glue between [go-containerstation](https://github.com/iaburton/go-containerstation) and the Java code used in this repo's Android project.

Please note that at the time of this writing, gomobile is broken with respect to go mods in go1.11.x. This means a few workarounds are required.
1. A ```GO111MODULE=off go get``` to put deps into your GOPATH.
2. ```GO111MODULE=off gomobile bind -o cstation.aar -target=android github.com/iaburton/cstation``` where `github.com/iaburton/cstation` is this package also in your GOPATH.
    * If working outside of your GOPATH a simple symlink with the above path into your GOPATH, from this directory, will work.

Afterwards the resulting cstation.aar can be copied into the cstation directory on the root of this repo, before building the Android app as normal in Android Studio.
This README assumes you have Android Studio and gomobile properly setup beforehand.
