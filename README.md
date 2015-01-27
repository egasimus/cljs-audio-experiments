# cljs-audio-experiments

Of course I haven't packaged this thing properly, so if you actually wanna run any of it, hit me up.
Or if you'd just like to marvel at the code, well that's fine too. At some point it featured, among other gimmicks:

* A volume mixer for PulseAudio, controlled by a DJTT Midi Fighter Twister.
* A Monome-like sample-slicing controller for a C audio sampler I wrote, controlled by a Novation Launchpad Mini.

Yeah, I'm one of _those_ people. Both experiments were good fun --
especially when I came across this "ambient noise" website which
happened to run 16 separately controllable audio streams out of Firefox.

##Things abandoned halfway:

* Automatically linking setting up a chain of audio plugins (plugins by dssi-vst or Carla, connected via JACK)
* Some clever, never-seen-before way of managing MIDI control for said plugins.
