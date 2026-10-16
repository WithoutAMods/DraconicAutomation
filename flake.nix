{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      nixpkgs,
      flake-utils,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells = {
          default =
            let
              libs = with pkgs; [
                libpulseaudio
                libGL
                glfw
                openal
                stdenv.cc.cc.lib
              ];
              libsPath = "/run/opengl-driver/lib:${pkgs.lib.makeLibraryPath libs}";
            in
            pkgs.mkShell {
              packages = with pkgs; [
                jdk21
              ];
              buildInputs = libs;
              shellHook = ''
                export LD_LIBRARY_PATH="${libsPath}:$LD_LIBRARY_PATH"
                export NIX_LD_LIBRARY_PATH="${libsPath}:$NIX_LD_LIBRARY_PATH"
              '';
            };
        };
      }
    );
}
