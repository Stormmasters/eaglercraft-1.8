#line 2

/*
 * Copyright (c) 2023 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

precision lowp int;
precision highp float;
precision highp sampler2D;
precision highp sampler2DShadow;

in vec2 v_position2f;

layout(location = 0) out float output1f;

uniform sampler2D u_gbufferDepthTexture;
uniform sampler2DShadow u_sunShadowDepthTexture;
uniform sampler2D u_ditherTexture;

uniform mat4 u_inverseViewProjMatrix4f;

uniform vec2 u_ditherScale2f;
uniform vec3 u_eyePosition3f;
uniform float u_sampleStep1f;

#define SAMPLES_PER_STEP 8.0
#define SAMPLES_PER_STEP_1 0.125

#ifdef COMPILE_SUN_SHADOW_LOD0
uniform mat4 u_sunShadowMatrixLOD04f;
#define SUN_SHADOW_MAP_FRAC 1.0
#endif
#ifdef COMPILE_SUN_SHADOW_LOD1
uniform mat4 u_sunShadowMatrixLOD04f;
uniform mat4 u_sunShadowMatrixLOD14f;
#define SUN_SHADOW_MAP_FRAC 0.5
#endif
#ifdef COMPILE_SUN_SHADOW_LOD2
uniform mat4 u_sunShadowMatrixLOD04f;
uniform mat4 u_sunShadowMatrixLOD14f;
uniform mat4 u_sunShadowMatrixLOD24f;
#define SUN_SHADOW_MAP_FRAC 0.3333333
#endif

float shadow(in vec3 coords) {
	vec4 shadowSpacePosition = u_sunShadowMatrixLOD04f * vec4(coords, 1.0);
	if(shadowSpacePosition.xyz == clamp(shadowSpacePosition.xyz, vec3(0.005), vec3(0.995))) {
		shadowSpacePosition.y *= SUN_SHADOW_MAP_FRAC;
		return textureLod(u_sunShadowDepthTexture, shadowSpacePosition.xyz, 0.0);
	}
#if defined(COMPILE_SUN_SHADOW_LOD1) || defined(COMPILE_SUN_SHADOW_LOD2)
	shadowSpacePosition = u_sunShadowMatrixLOD14f * vec4(coords, 1.0);
	if(shadowSpacePosition.xyz == clamp(shadowSpacePosition.xyz, vec3(0.005), vec3(0.995))) {
		shadowSpacePosition.y += 1.0;
		shadowSpacePosition.y *= SUN_SHADOW_MAP_FRAC;
		return textureLod(u_sunShadowDepthTexture, shadowSpacePosition.xyz, 0.0);
	}
#endif
#ifdef COMPILE_SUN_SHADOW_LOD2
	shadowSpacePosition = u_sunShadowMatrixLOD24f * vec4(coords, 1.0);
	if(shadowSpacePosition.xyz == clamp(shadowSpacePosition.xyz, vec3(0.005), vec3(0.995))) {
		shadowSpacePosition.y += 2.0;
		shadowSpacePosition.y *= SUN_SHADOW_MAP_FRAC;
		return textureLod(u_sunShadowDepthTexture, shadowSpacePosition.xyz, 0.0);
	}
#endif
	return -1.0;
}

#define STEP2DST(stepNum) (stepNum * stepNum * 0.06 + stepNum * 0.05)

void main() {
	output1f = 0.0;
	float depth = textureLod(u_gbufferDepthTexture, v_position2f, 0.0).r;
	if(depth == 0.0) {
		return;
	}

	vec4 fragPos4f = vec4(v_position2f, depth, 1.0);
	fragPos4f.xyz *= 2.0;
	fragPos4f.xyz -= 1.0;

	fragPos4f = u_inverseViewProjMatrix4f * fragPos4f;
	fragPos4f.xyz /= fragPos4f.w;
	fragPos4f.w = 1.0;
	fragPos4f.xyz -= u_eyePosition3f;

	float viewDist = length(fragPos4f.xyz);
	fragPos4f.xyz /= viewDist;
	float sampleNum = textureLod(u_ditherTexture, u_ditherScale2f * v_position2f, 0.0).r;
	sampleNum += u_sampleStep1f * SAMPLES_PER_STEP + 1.0;

	float cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

	sampleNum += 1.0;
	cloudSample = STEP2DST(sampleNum);
	if(cloudSample > viewDist) return;
	cloudSample = shadow(u_eyePosition3f + fragPos4f.xyz * cloudSample);
	if(cloudSample < 0.0) return;
	output1f += cloudSample * SAMPLES_PER_STEP_1;

}
