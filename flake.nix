{
  description = "Flappy - a small arcade game built with Java and LWJGL";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { nixpkgs, ... }:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
      jdk = pkgs.jdk17;

      # Native libraries that GLFW and OpenGL load at runtime through LWJGL
      runtimeLibs = with pkgs; [
        libGL
        libx11
        libxcursor
        libxext
        libxi
        libxinerama
        libxrandr
        libxxf86vm
      ];
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        packages = [ jdk pkgs.maven pkgs.gh ];

        JAVA_HOME = jdk.home;
        LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath runtimeLibs;

        shellHook = ''
          bold=$'\e[1m'; dim=$'\e[2m'; reset=$'\e[0m'
          rule="━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

          echo ""
          echo "$rule"
          echo "  🐦 ''${bold}FLAPPY''${reset}  ''${dim}·  development shell''${reset}"
          echo "$rule"
          echo "  ☕ JDK     ${jdk.version}"
          echo "  📦 Maven   ${pkgs.maven.version}"
          echo "  🐙 gh      ${pkgs.gh.version}"
          echo ""
          echo "  ▶️  ''${bold}mvn package && java -jar target/flappy.jar''${reset}"
          echo "$rule"
          echo ""
        '';
      };
    };
}
