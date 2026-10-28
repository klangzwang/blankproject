#version 150

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

// Uniforms for SDF Rounded Box with Border, Gradient, and Drop Shadow
uniform vec2 RectSize;          // Width & Height of the main container in pixels
uniform vec4 BgColorTop;        // RGBA color at the top of the background gradient
uniform vec4 BgColorBottom;     // RGBA color at the bottom of the background gradient
uniform vec4 BorderColor;       // RGBA border color
uniform float Radius;           // Corner radius in pixels
uniform float BorderThickness;  // Border thickness in pixels
uniform vec2 ShadowOffset;      // X/Y offset of the drop shadow in pixels
uniform float ShadowBlur;       // Blur radius/softness of the drop shadow in pixels
uniform vec4 ShadowColor;       // RGBA color and alpha of the drop shadow

/**
 * Signed Distance Function for a 2D rounded box.
 * p: coordinate centered at the box origin (0, 0)
 * b: half-extents of the box (width / 2, height / 2)
 * r: corner radius
 */
float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

void main() {
    // texCoord is in [0, 1] normalized quad space
    // Total quad size includes shadow padding:
    // padding = ShadowBlur * 2.0 + max(abs(ShadowOffset.x), abs(ShadowOffset.y))
    float pad = max(ShadowBlur * 2.5 + max(abs(ShadowOffset.x), abs(ShadowOffset.y)), 1.0);
    vec2 totalSize = RectSize + vec2(pad * 2.0);

    // Convert normalized texCoord to local pixel coordinates centered at (0, 0) of the main rectangle
    vec2 pixelCoord = (texCoord - 0.5) * totalSize;

    vec2 halfSize = RectSize * 0.5;
    float effectiveRadius = clamp(Radius, 0.0, min(halfSize.x, halfSize.y));

    // -------------------------------------------------------------
    // 1. DROP SHADOW (Behind the main rectangle)
    // -------------------------------------------------------------
    vec2 shadowCoord = pixelCoord - ShadowOffset;
    float distShadow = sdRoundedBox(shadowCoord, halfSize, effectiveRadius);

    float shadowFactor = 0.0;
    if (ShadowColor.a > 0.0) {
        if (ShadowBlur > 0.0) {
            // Smooth Gaussian-like falloff via smoothstep
            shadowFactor = 1.0 - smoothstep(-ShadowBlur * 0.4, ShadowBlur * 1.2, distShadow);
        } else {
            shadowFactor = 1.0 - smoothstep(-0.5, 0.5, distShadow);
        }
    }
    vec4 shadow = vec4(ShadowColor.rgb, ShadowColor.a * shadowFactor);

    // -------------------------------------------------------------
    // 2. MAIN ROUNDED RECTANGLE (SDF & Anti-Aliasing)
    // -------------------------------------------------------------
    float distBox = sdRoundedBox(pixelCoord, halfSize, effectiveRadius);

    // Anti-aliased outer edge mask
    float aa = 1.0;
    #ifdef GL_OES_standard_derivatives
    aa = fwidth(distBox);
    #endif
    aa = max(aa, 0.7);

    float outerAlpha = 1.0 - smoothstep(-aa * 0.5, aa * 0.5, distBox);

    // -------------------------------------------------------------
    // 3. BACKGROUND GRADIENT (Vertical from BgColorTop to BgColorBottom)
    // -------------------------------------------------------------
    float gradientT = clamp((pixelCoord.y + halfSize.y) / max(RectSize.y, 1.0), 0.0, 1.0);
    vec4 bgColor = mix(BgColorTop, BgColorBottom, gradientT);

    // -------------------------------------------------------------
    // 4. DYNAMIC BORDER (SDF Inner Edge)
    // -------------------------------------------------------------
    vec4 shapeColor = bgColor;
    if (BorderThickness > 0.0) {
        float distInner = distBox + BorderThickness;
        float innerAlpha = 1.0 - smoothstep(-aa * 0.5, aa * 0.5, distInner);
        float borderFactor = clamp(outerAlpha - innerAlpha, 0.0, 1.0);

        // Composite border over background
        shapeColor = mix(bgColor, BorderColor, borderFactor);
    }

    // Apply vertexColor tint if passed from Minecraft render system
    shapeColor *= vertexColor;

    // -------------------------------------------------------------
    // 5. COMPOSITING: Main Shape over Drop Shadow
    // -------------------------------------------------------------
    // Standard Porter-Duff Over: shape OVER shadow
    float shapeA = outerAlpha * shapeColor.a;
    vec3 finalRgb = shapeColor.rgb * shapeA + shadow.rgb * shadow.a * (1.0 - shapeA);
    float finalAlpha = shapeA + shadow.a * (1.0 - shapeA);

    if (finalAlpha <= 0.001) {
        discard;
    }

    fragColor = vec4(finalRgb / max(finalAlpha, 0.0001), finalAlpha);
}
